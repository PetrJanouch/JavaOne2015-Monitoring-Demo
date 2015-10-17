var graphs1 = [];
var palette = ["#1F7872", "#129793", "#9BD7D5", "#993300", "#CF5300", "#FF7619"];
var nodeLabels = [];

// init
$(function () {
    initGraps();

    graphs1[0] = new Graph1(1);
    graphs1[1] = new Graph1(2);
    graphs1[2] = new Graph1(3);

    initSse();
});

function initGraps() {
    var node1 = $('#node1');
    nodeLabels[0] = node1.find(".serverLabel");
    var node2 = node1.clone();
    node2.attr('id', 'node2');
    node2.appendTo('#nodes');
    var label2 = node2.find(".serverLabel");
    label2.html("Server 2");
    nodeLabels[1] = label2;

    var node3 = node1.clone();
    node3.attr('id', 'node3');
    node3.appendTo('#nodes');
    var label3 = node3.find(".serverLabel");
    label3.html("Server 3");
    nodeLabels[2] = label3;
}

function getUri() {
    var host = window.location.host;
    return "http://" + host + "/app/statistics";
}

function initSse() {
    var uri = getUri();
    var source = new EventSource(uri);

    source.onmessage = function (evt) {
        var values = JSON.parse(evt.data);
        for (var node = 0; node < 3; node++) {
            var nodeValues = values[node];
            nodeLabels[node].html(nodeValues.node);
            var requestsPerResource = nodeValues.requestsPerResource;
            graphs1[node].update([requestsPerResource.MonitoredResource1, requestsPerResource.MonitoredResource2]);
        }
    };
}

function Graph1(nodeId) {

    var dataSeries = new DataSeries();
    var location = $('#node' + nodeId + ' .graph1')[0];
    var graphData = dataSeries.data;

// instantiate our graph!
    var graph = new Rickshaw.Graph({
        element: location,
        renderer: 'area',
        stroke: true,
        preserve: true,
        series: [
            {
                color: palette[0],
                data: graphData[0],
                name: 'MonitoredResource1'
            },
            {
                color: palette[3],
                data: graphData[1],
                name: 'MonitoredResource2'
            }

        ]
    });

    graph.render();

    var ticksTreatment = 'glow';
    var xAxis = new Rickshaw.Graph.Axis.Time({
        graph: graph,
        ticksTreatment: ticksTreatment,
        timeFixture: new Rickshaw.Fixtures.Time.Local()
    });

    xAxis.render();

    var yAxis = new Rickshaw.Graph.Axis.Y({
        graph: graph,
        tickFormat: Rickshaw.Fixtures.Number.formatKMBT,
        ticksTreatment: ticksTreatment
    });

    yAxis.render();
    if (nodeId == 1) {
        var legend = new Rickshaw.Graph.Legend({
            graph: graph,
            element: document.getElementById('legend')
        });
    }

    this.update = function (values) {
        dataSeries.addValues(values);
        graph.update();
    }
}

// series of data used for plotting graph1
function DataSeries() {

    this.data = [
        [],
        [],
        [],
        [],
        [],
        []
    ];

    // in seconds
    var interval = 1;
    var bufferSize = 180;
    var initTime = getTime() - bufferSize * interval;

    this.data.forEach(function (series) {
        for (var i = 0; i < bufferSize; i++) {
            series.push({x: initTime + i * interval, y: 0});
        }
    });

    function getTime() {
        return Math.floor(new Date().getTime() / 1000);
    }

    // add newly arrived data to the series
    this.addValues = function (values) {

        this.data.forEach(function (series) {
            series.shift();
        });

        var i = 0;
        this.data.forEach(function (series) {
            series.push({x: getTime(), y: values[i]});
            i++;
        });
    };
}