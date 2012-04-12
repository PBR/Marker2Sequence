/*!
 * BreeDB jQuery JavaScript Library v0.1
 *
 * Copyright 2010, Richard Finkers
 *
 * Date: Wed Jan 20 13:46
 */

// unblock when ajax activity stops
$(document).ajaxStop($.unblockUI);

function test() {
    $.ajax({
        url: 'wait.jsp',
        cache: false
    });
}

// Just a moment message
$(document).ready(function() {
    $('#submitMoment').click(function() {
        $.blockUI({
            //TODO: image
            //message: '<h2><img src="/images/busy.gif" /> Just a moment...</h2>',
            message: '<h2><span class=".ui-autocomplete-loading">Just a moment...</span></h2>',
            css: {
                border: '2px solid #C71400'
            }
        });
    //test();
    });
});

//
$(document).ready(function() {
    $('#submitProcessing').click(function() {
        $.blockUI({
            //TODO: images
            //message: '<h2><img src="/images/busy.gif" /> The result of your analysis is being computed.<br/> Please be patient.</h2>',
            message: '<h2><span class=".ui-autocomplete-loading">The result of your analysis is being computed.<br/> Please be patient.</span></h2>',
            css: {
                border: '2px solid #C71400'
            }
        });
    //test();
    });
});


// Tab javascript function
$(function() {
    $("#tabs").tabs();
});
// Tab javascript function
$(function() {
    $("#tabsimg").tabs();
});

$(function() {
    // run the currently selected effect
    function runToggle() {
        // run the effect
        $( "#ToggleHelpM2S" ).toggle( "blind", "", 500);
    };
    function callback() {
        setTimeout(function() {
            $( "#effect:visible" ).removeAttr( "style" ).fadeOut();
        }, 1000 );
    };

    // set effect from select menu value
    $( "#toggleButton" ).click(function() {
        runToggle();
        return false;
    });

    $( "#ToggleHelpM2S" ).hide();
});