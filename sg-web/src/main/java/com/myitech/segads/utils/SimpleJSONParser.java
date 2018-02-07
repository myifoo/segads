package com.myitech.segads.utils;

import java.util.*;

/**
 * Created by A.T on 2018/1/22.
 */
public class SimpleJSONParser {
    private final String input;
    private int idx;

    private SimpleJSONParser(String input) {
        this.input = input;
    }

    public static List<String> parseStringList(String input) {
        if (input == null || input.isEmpty())
            return Collections.<String>emptyList();

        List<String> output = new ArrayList<String>();
        SimpleJSONParser parser = new SimpleJSONParser(input);
        if (parser.nextCharSkipSpaces() != '[')
            throw new IllegalArgumentException("Not a JSON list: " + input);

        char c = parser.nextCharSkipSpaces();
        if (c == ']')
            return output;

        while (true) {
            assert c == '"';
            output.add(parser.nextString());
            c = parser.nextCharSkipSpaces();
            if (c == ']')
                return output;
            assert c == ',';
            c = parser.nextCharSkipSpaces();
        }
    }

    public static Map<String, String> parseStringMap(String input) {
        if (input == null || input.isEmpty())
            return Collections.<String, String>emptyMap();

        Map<String, String> output = new HashMap<String, String>();
        SimpleJSONParser parser = new SimpleJSONParser(input);
        if (parser.nextCharSkipSpaces() != '{')
            throw new IllegalArgumentException("Not a JSON map: " + input);

        char c = parser.nextCharSkipSpaces();
        if (c == '}')
            return output;

        while (true) {
            assert c == '"';
            String key = parser.nextString();
            c = parser.nextCharSkipSpaces();
            assert c == ':';
            c = parser.nextCharSkipSpaces();
            assert c == '"';
            String value = parser.nextString();
            output.put(key, value);
            c = parser.nextCharSkipSpaces();
            if (c == '}')
                return output;
            assert c == ',';
            c = parser.nextCharSkipSpaces();
        }
    }

    /**
     * Read the next char, the one at position idx, and advance ix.
     */
    private char nextChar() {
        if (idx >= input.length())
            throw new IllegalArgumentException("Invalid json input: " + input);
        return input.charAt(idx++);
    }

    /**
     * Same as nextChar, except that it skips space characters (' ', '\t' and '\n').
     */
    private char nextCharSkipSpaces() {
        char c = nextChar();
        while (c == ' ' || c == '\t' || c == '\n')
            c = nextChar();
        return c;
    }

    /**
     * Reads a String, assuming idx is on the first character of the string (i.e. the
     * one after the opening double-quote character).
     * After the string has been read, idx will be on the first character after
     * the closing double-quote.
     */
    private String nextString() {
        assert input.charAt(idx - 1) == '"' : "Char is '" + input.charAt(idx - 1) + '\'';
        StringBuilder sb = new StringBuilder();
        while (true) {
            char c = nextChar();
            switch (c) {
                case '\n':
                case '\r':
                    throw new IllegalArgumentException("Unterminated string");
                case '\\':
                    c = nextChar();
                    switch (c) {
                        case 'b':
                            sb.append('\b');
                            break;
                        case 't':
                            sb.append('\t');
                            break;
                        case 'n':
                            sb.append('\n');
                            break;
                        case 'f':
                            sb.append('\f');
                            break;
                        case 'r':
                            sb.append('\r');
                            break;
                        case 'u':
                            sb.append((char) Integer.parseInt(input.substring(idx, idx + 4), 16));
                            idx += 4;
                            break;
                        case '"':
                        case '\'':
                        case '\\':
                        case '/':
                            sb.append(c);
                            break;
                        default:
                            throw new IllegalArgumentException("Illegal escape");
                    }
                    break;
                default:
                    if (c == '"')
                        return sb.toString();
                    sb.append(c);
            }
        }
    }
}
