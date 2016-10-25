$( "a[href^='http://']" ).attr( "target", "_blank" );
$( "a[href^='https://']" ).attr( "target", "_blank" );
$( "a[href^='#']" ).attr( "target", "" );
$( "a" ).on('click', function(event){ event.stopPropagation(); });

$('img').each(function(index){
    var src = $(this).attr('src');
    $(this).attr({
        src: "data:image/gif;base64,R0lGODlhAQABAAAAACH5BAEKAAEALAAAAAABAAEAAAICTAEAOw==",
        'data-src': src,
        onload: "lzld(this)"
    });
});

$('a').each(function(index){
    var href = $(this).attr('href');
    if( href.endsWith('.md') ) {
        var newhref = href.substr(0, href.length-3) + '.html';
        $(this).attr({ 'href': newhref });
    }
});

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
    if(getSelection().toString()){ return; }
    if($('body').hasClass('locked')){ return; }
    var $dt = $(this).prev('dt');
    if ($dt.hasClass('drawer')) {
        if( !$dt.hasClass('focus') ) {
            $('.focus').removeClass('focus');
            $dt.addClass('focus');
            $(this).addClass('focus');
            return event.stopPropagation();
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

$('body').addClass('foldable').removeClass('locked');
