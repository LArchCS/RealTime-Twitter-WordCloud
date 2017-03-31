var color = d3.scale.ordinal()
        .domain([100, 65, 60, 55, 50, 45, 44, 43, 42, 41, 40])
        .range(["#ddd", "#ccc", "#bbb", "#aaa", "#999", "#888", "#777", "#666", "#555", "#444", "#00ffc6", "#222"]);

d3.layout.cloud().size([1800, 600])
        .words(wordList)
        .rotate(0)
        .fontSize(function(x) {return x.size; })
        .on("end", draw)
        .start();

function draw(words) {
    d3.select("body").append("svg")
        .attr("width", 850)
        .attr("height", 350)
        .append("g")
        // must transform otherwise texts will go off of svg
        .attr("transform", "translate(" + (850 / 2) + ", " +  (350 / 2) + ")")
        .selectAll("text")
        .data(words)
        .enter().append("text")
        .style("font-size", function(x) { return x.size + "px"; })
        .style("fill", function(x, i) { return color(i); })
        .attr("transform", function(x) {
            return "translate(" + [x.x, x.y] + ")rotate(" + x.rotate + ")";
        })
        .text(function(x) { return x.text; });
};
