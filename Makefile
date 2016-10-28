.PHONY: all clean md2html

all: md2html.jar
clean:
	rm -rf out
	rm md2html.jar
md2html: md2html.jar
	java -jar md2html.jar

dist := out/production/md2html
pack := \
	$(dist)/META-INF/MANIFEST.MF \
	$(dist)/com/tangzhixiong/java/Bundle$1.class \
	$(dist)/com/tangzhixiong/java/Bundle.class \
	$(dist)/com/tangzhixiong/java/DirectoryListing.class \
	$(dist)/com/tangzhixiong/java/Main.class \
	$(dist)/com/tangzhixiong/java/Utility.class \
	$(dist)/.md2html.yml \
	$(dist)/README.txt \
	$(dist)/cat.pl \
	$(dist)/drawer.pl \
	$(dist)/html.template \
	$(dist)/jquery-3.0.0.min.js \
	$(dist)/lazyload.min.js \
	$(dist)/main.css \
	$(dist)/main.js \

%.class:
	mkdir -p $(dist)
	javac src/com/tangzhixiong/java/*.java -d $(dist)
$(dist)/%: tools/%
	@mkdir -p $(@D)
	cp $< $@
$(dist)/%: src/%
	@mkdir -p $(@D)
	cp $< $@
md2html.jar: $(pack)
	(cd $(dist) && zip -r ../md2html.jar * && cp ../md2html.jar ../../../)
