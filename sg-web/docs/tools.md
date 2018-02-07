# curl

> Curl is a perfect tool for restful api testing!

-X/--request [GET|POST|PUT|DELETE|…]  使用指定的http method
-H/--header                           设置request里的header信息，比如content-type
-i/--include                          显示response的header
-d/--data                             设置 http parameters
-v/--verbose                          输出详细信息
-u/--user                             指定使用者账户，密码
-b/--cookie                           cookie

## request method
curl -X GET "http://www.rest.com/api/users"

## header
curl -v -i -H "Content-Type: application/json" http://www.example.com/users

## data
1. curl -X POST -d "param1=value1&param2=value2"
2. curl -X POST -d "param1=value1" -d "param2=value2"

## json
1. curl http://www.example.com?modifier=kent -X PUT -i -H "Content-Type:application/json" -H "Accept:application/json" -d '{"boolean" : false, "foo" : "bar"}'
2. curl http://www.example.com?modifier=kent -X PUT -i -H "Content-Type:application/json" -d '{"boolean" : false, "foo" : "bar"}'

## cookie
许多服务，需要进行登陆或者认证，然后通过cookie或session信息来访问
> curl -X GET 'http://www.rest.com/api/users' --header 'sessionid:1234567890987654321'

如果使用cookie，可以把cookie信息存档，然后通过 -b cookie_file 的方式在request中植入cookie

存档
> curl -i -X POST -d username=kent -d password=kent123 -c  ~/cookie.txt  http://www.rest.com/auth

使用
> curl -i --header "Accept:application/json" -X GET -b ~/cookie.txt http://www.rest.com/users/1

## upload file

> curl -i -X POST -F 'file=@/Users/kent/my_file.txt' -F 'name=a_file_name'

1. curl -X DELETE "localhost:9998/session"
2. curl.exe -X POST "http://localhost/segads/session/anteng/general/property" -d "key1=aaaa&key2=bbbbb"
