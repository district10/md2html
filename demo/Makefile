all: configs.html README.html
%.html: %.org
	pandoc -S -s --ascii -f org+east_asian_line_breaks+emoji -V rootdir=./ -V md2htmldir=_md2html/ -V thispath=configs.org --template _md2html/html.template $< -o $@
