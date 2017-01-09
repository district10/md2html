$( "a[href^='http://']" ).attr( "target", "_blank" );
$( "a[href^='https://']" ).attr( "target", "_blank" );
$( "a[href^='#']" ).attr( "target", "" );
$( "a" ).on('click', function(event){ event.stopPropagation(); });
$("a").each(function(index){
    var href = $(this).attr('href');
    if( href.endsWith('.md') ) {
        var newhref = href.substr(0, href.length-3) + '.html';
        $(this).attr({ 'href': newhref });
    }
});

$('img').each(function(index){
    var src = $(this).attr('src');
    $(this).attr({
        src: "data:image/gif;base64,R0lGODlhAQABAAAAACH5BAEKAAEALAAAAAABAAEAAAICTAEAOw==",
        'data-src': src,
        onload: "lzld(this)"
    });
});

$("code").each(function () {
    var text = $(this).text();
    if ($(this).parent().is("pre")) {
        var $btn = $('<button class="copy-me">Copy</button>');
        $btn.attr({
            "data-clipboard-text": text
        });
        $btn.appendTo($(this).parent());
    } else {
        $(this).attr({
            "data-clipboard-text": text
        }).addClass("copy-me");
    }
});
var clipboard = new Clipboard(".copy-me");
/*
clipboard.on('success', function(e) {
    console.info('Action:', e.action);
    console.info('Text:', e.text);
    console.info('Trigger:', e.trigger);
    e.clearSelection();
});
clipboard.on('error', function(e) {
    console.error('Action:', e.action);
    console.error('Trigger:', e.trigger);
});
*/

$('dt > code.fold').each(function(){
    $(this)
        .parent().addClass('drawerClose').addClass('drawer')
        .next('dd').addClass('drawerHide');
});
$('dt > code.foldable').each(function(){
    $(this)
        .parent().addClass('drawerOpen').addClass('drawer');
});
$('dt.drawer').on('click', function(event){
    if(getSelection().toString()){ return; }
    if($('body').hasClass('locked')){ return; }
    $('.focus').removeClass('focus');
    $(this).addClass('focus').next('dd').addClass('focus');
    $(this)
        .toggleClass('drawerOpen')
        .toggleClass('drawerClose');
    $(this).next('dd').toggleClass('drawerHide');
    event.stopPropagation();
});
$('dd').on('click', function(event){
    if ($(event.target).hasClass("copy-me")) { return; }
    if(getSelection().toString()){ return; }
    if($('body').hasClass('locked')){ return; }
    var $dt = $(this).prev('dt');
    if ($dt.hasClass('drawer')) {
        if( !$dt.hasClass('focus') ) {
            $('.focus').removeClass('focus');
            $dt.addClass('focus');
            $(this).addClass('focus');
            event.stopPropagation();
            return;
        }
        $('.focus').removeClass('focus');
        $(this).addClass('focus').prev('dt').addClass('focus');
        $dt
            .toggleClass('drawerOpen')
            .toggleClass('drawerClose');
        if ($(this).toggleClass('drawerHide').hasClass('drawerHide')) {
            if ( $(this).offset().top < pageYOffset ) {
                $('body,html').animate({scrollTop:$dt.offset().top},300);
            }
        }
        event.stopPropagation();
    }
});

$('body').on('click', function(event){
    if(getSelection().toString()){ return; }
    if($('body').hasClass('locked')){ return; }
    $('.focus').removeClass('focus');
});

function expandAll() {
    $('dt.drawerClose').removeClass('drawerClose').addClass('drawerOpen').next('dd').removeClass('drawerHide');
}
function foldAll() {
    $('dt.drawerOpen').removeClass('drawerOpen').addClass('drawerClose').next('dd').addClass('drawerHide');
}

function loadFragment(url) {
    $('body.markdown-body').load(url, function( response, status, xhr )  {
        console.log('status: '+status);
        console.log('xhr: '+JSON.stringify(xhr));
    });
}

function loadIndex() {
    // var url = metaJson.rootdir+metaJson.md2htmldir+'index.html';
    var url = metaJson.rootdir+'index.html';
    window.location = url;
}
