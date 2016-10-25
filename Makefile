.PHONY: all clean md2html

funmd2html = cat $(1) | pandoc \
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
	rm -rf publish
	rm -rf out
	rm md2html.jar
publish/%.html: %.md
	@mkdir -p $(@D)
	$(call funmd2html, $<, $@)
publish/index.html: README.md
	@mkdir -p $(@D)
	$(call funmd2html, $<, $@)
publish/%: %
	@mkdir -p $(@D)
	cp $< $@
publish/%: tools/%
	@mkdir -p $(@D)
	cp $< $@

dist := out/Production
pack := \
	$(dist)/META-INF/MANIFEST.MF \
	$(dist)/com/tangzhixiong/java/Bundle$1.class \
	$(dist)/com/tangzhixiong/java/Bundle.class \
	$(dist)/com/tangzhixiong/java/DirectoryListing.class \
	$(dist)/com/tangzhixiong/java/Main.class \
	$(dist)/com/tangzhixiong/java/Utility.class \
	$(dist)/cat.pl \
	$(dist)/drawer.pl \
	$(dist)/html.template \
	$(dist)/jquery-3.0.0.min.js \
	$(dist)/lazyload.min.js \
	$(dist)/main.css \
	$(dist)/main.js \
	$(dist)/README.txt \

*.class:
	mkdir -p $(dist)
	javac src/com/tangzhixiong/java/*.java -d $(dist)

$(dist)/%: tools/%
	@mkdir -p $(@D)
	cp $< $@
$(dist)/%: src/%
	@mkdir -p $(@D)
	cp $< $@

md2html.jar: $(pack)
	(cd $(dist) && zip -r ../md2html.jar * && cp ../md2html.jar ../../)

md2html: md2html.jar
	java -jar md2html.jar
