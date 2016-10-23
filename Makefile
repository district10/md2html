.PHONY: all clean

md2html = cat $(1) | pandoc \
		-V rootdir=./ \
		-S -s --ascii --mathjax \
		-f markdown+abbreviations+east_asian_line_breaks+emoji \
		--template tools/html.template \
		-o $(2)

SRC:=$(wildcard *.md)
DST:=$(addprefix publish/, $(SRC:%.md=%.html)) publish/index.html
CSSJS:=$(wildcard tools/*.js) tools/main.css
STATICSSRC:=$(wildcard cover/*) $(wildcard images/*) $(CSSJS:tools/%=%)
STATICSDST:=$(addprefix publish/, $(STATICSSRC))

all: $(DST) $(STATICSDST)
clean:
	rm -rf publish/*
publish/%.html: %.md
	@mkdir -p $(@D)
	$(call md2html, $<, $@)
publish/index.html: README.md
	@mkdir -p $(@D)
	$(call md2html, $<, $@)
publish/%: %
	@mkdir -p $(@D)
	cp $< $@
publish/%: tools/%
	@mkdir -p $(@D)
	cp $< $@
