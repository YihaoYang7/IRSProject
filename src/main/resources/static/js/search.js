function getInputSuggestion() {
    let characters = $('#autoComplete').val();
    if (characters === '')
        return;
    let requestData = {
        keywords: characters
    };
    $.ajax({
        type: 'post',
        url: '/input',
        data: JSON.stringify(requestData),
        dataType: 'json',
        contentType: 'application/json',
        success: function (data) {
            $("#inputSuggestion").html("");
            if (data !== "null" && data !== undefined) {
                for (let i = 0; i < data.length; i++) {
                    for (let j = 0; j < data[i].length; j++) {
                        $("#inputSuggestion").append('<div class="item" onblur="lost()" onclick="mousedown(this)">' + data[i][j] +
                            '</div>');
                    }
                }
                $("#inputSuggestion").slideDown();
            }
        }
    })
}

function mousedown(object) {
    $("#autoComplete").val($(object).text());
    $("#inputSuggestion").fadeOut();
}

function lost() {
    $("#inputSuggestion").fadeOut();
}

$("#submitButton").click(function () {
    $("#rd").find("*").remove();
    $("#inputSuggestion").fadeOut();
    var k = $("#autoComplete").val();
    var data = {
        keywords: k
    };
    if (k === '') {
        layer.msg('Please type keywords', {skin: 'layui-layer-molv'}, {time: 2})
        return;
    }
    $.ajax({
        type: 'post',
        url: '/query2',
        data: JSON.stringify(data),
        dataType: 'json',
        contentType: 'application/json',
        success: function (queryResults) {
            for (let i = 0; i < queryResults.length; i++) {
                var title = queryResults[i].title;
                var authors = queryResults[i].authors;
                var sources = queryResults[i].sources;
                var publishedDate = queryResults[i].date;
                var html = '<div class="result-border"><p class="result-title">Title:' + title +
                    '</p><p class="result-title">Authors:' + authors + '</p><p class="result-title">Date:' + publishedDate + '</p>'
                if (sources !== undefined) {
                    for (let j = 0; j < sources.length; j++) {
                        let linkS = sources[j];
                        html = html.concat('<a href="' + linkS + '">' + linkS + '</a>');
                    }
                }
                html.concat('</div>')
                // $('#rd').append(html);
                $(html).appendTo("#rd");
            }

            let loadButton = $("#loadButton");
            if (loadButton !== null) {
                $("#loadButton").remove();
                loadButton = '<button id="loadButton" class="btn layui-btn-primary">Load more results</button>';
                $(loadButton).appendTo(".continue-search-block");
            } else {
                loadButton = '<button id="loadButton" class="btn layui-btn-primary">Load more results</button>';
                $(loadButton).appendTo(".continue-search-block");
            }
        }
    });
})

$(".continue-search-block").on('click', '#loadButton', function () {
    var k = $("#autoComplete").val();
    var data = {
        keywords: k
    };
    $.ajax({
        type: 'post',
        url: '/query2',
        data: JSON.stringify(data),
        dataType: 'json',
        contentType: 'application/json',
        success: function (queryResults) {
            for (let i = 0; i < queryResults.length; i++) {
                var title = queryResults[i].title;
                var authors = queryResults[i].authors;
                var sources = queryResults[i].sources;
                var publishedDate = queryResults[i].date;
                var html = '<div class="result-border"><p class="result-title">Title:' + title +
                    '</p><p class="result-title">Authors:' + authors + '</p><p class="result-title">Date:' + publishedDate + '</p>'
                if (sources !== undefined) {
                    for (let j = 0; j < sources.length; j++) {
                        let linkS = sources[j];
                        html = html.concat('<a href="' + linkS + '">' + linkS + '</a>');
                    }
                }
                html.concat('</div>')
                $(html).appendTo("#rd");
            }

            let loadButton = $("#loadButton");
            if (loadButton !== null) {
                $("#loadButton").remove();
                loadButton = '<button id="loadButton" class="btn layui-btn-primary">Load more results</button>';
                $(loadButton).appendTo(".continue-search-block");
            } else {
                loadButton = '<button id="loadButton" class="btn layui-btn-primary">Load more results</button>';
                $(loadButton).appendTo(".continue-search-block");
            }
        }
    });
})
