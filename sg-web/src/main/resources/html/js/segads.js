function dispatcher(){
    var args = $("#command_line").val().split(" ")
    var action = args[0];

    if (action == "push"){
        push(args);
    }
    else if(action == "show") {
        show(args);
    }else if(action == "forecast") {
        var body = $("#command_line").val().substring(action.length+1)
        forecast(body)
    }else if(action == "demo") {
        var body = $("#command_line").val().substring(action.length+1)
        demo(body)
    }
}

function push(args) {
    var id = args[1];
    var url = args[2];
    $.getJSON(url, function (data) {
        params = {};
        params[id] = data;

        $.ajax({
            url: "/segads/data/update",
            type: "POST",
            data: JSON.stringify(params),
            contentType: 'application/json',
            success: function(result){
                console.log(result)
            },
            error: function(error){
                console.log(error)
            }
        })
    })
}

function plot(series) {
    Highcharts.chart('data_container', {
        chart: {
            zoomType: 'x'
        },
        title: {
            text: '美元兑欧元汇率走势图'
        },
        subtitle: {
            text: document.ontouchstart === undefined ?
                '鼠标拖动可以进行缩放' : '手势操作进行缩放'
        },
        xAxis: {
            type: 'datetime',
            dateTimeLabelFormats: {
                millisecond: '%H:%M:%S.%L',
                second: '%H:%M:%S',
                minute: '%H:%M',
                hour: '%H:%M',
                day: '%m-%d',
                week: '%m-%d',
                month: '%Y-%m',
                year: '%Y'
            }
        },
        tooltip: {
            dateTimeLabelFormats: {
                millisecond: '%H:%M:%S.%L',
                second: '%H:%M:%S',
                minute: '%H:%M',
                hour: '%H:%M',
                day: '%Y-%m-%d',
                week: '%m-%d',
                month: '%Y-%m',
                year: '%Y'
            }
        },
        yAxis: {
            title: {
                text: '汇率'
            }
        },
        legend: {
            enabled: false
        },
        plotOptions: {
            area: {
                fillColor: {
                    linearGradient: {
                        x1: 0,
                        y1: 0,
                        x2: 0,
                        y2: 1
                    },
                    stops: [
                        [0, Highcharts.getOptions().colors[0]],
                        [1, Highcharts.Color(Highcharts.getOptions().colors[0]).setOpacity(0).get('rgba')]
                    ]
                },
                marker: {
                    radius: 2
                },
                lineWidth: 1,
                states: {
                    hover: {
                        lineWidth: 1
                    }
                },
                threshold: null
            }
        },
        series: series
    });
}

function show(args){
    var id = args[1]
    $.getJSON("/segads/data/"+id, function (data) {
        plot([{
            type: 'area',
            name: '美元兑欧元',
            data: data
        }])
    });
}

function demo(body) {
    $.ajax({
        url: "/segads/model/demo",
        type: "POST",
        data: body,
        contentType: 'application/json',
        success: function(data_set){
            plot([
                {
                    type: 'area',
                    name: '美元兑欧元',
                    data: data_set["source_series"]
                },
                {
                    type: 'area',
                    name: data_set["forecast_type"],
                    data: data_set["target_series"]
                }
            ])
        },
        error: function(error){
            console.log(error)
        }
    })
}

function forecast(body) {
    $.ajax({
        url: "/segads/model/forecast",
        type: "POST",
        data: body,
        contentType: 'application/json',
        success: function(data_set){
            plot([
                    {
                        type: 'area',
                        name: '美元兑欧元',
                        data: data_set["source_series"]
                    },
                    {
                        type: 'area',
                        name: data_set["forecast_type"],
                        data: data_set["target_series"]
                    }
                ])
        },
        error: function(error){
            console.log(error)
        }
    })

}