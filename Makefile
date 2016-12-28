all: target/md2html.jar
target/md2html.jar: $(wildcard src/main/java/com/tangzhixiong/md2html/*.java)
	mvn package
	rm publish/ -rf
clean:
	rm -rf publish/ target/
test: publish/md2html.jar
	java -jar $< -i src/test/input -o publish
publish/%: target/%
	mkdir -p $(@D)
	cp $< $@
gh:
	git add -A; git commit && git push;
