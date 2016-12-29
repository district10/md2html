all: target/md2html.jar
target/md2html.jar: $(wildcard src/main/java/com/tangzhixiong/md2html/*.java) $(wildcard src/main/resources/*)
	mvn package
	rm publish/ -rf
clean:
	rm -rf publish/ target/
test: publish/md2html.jar
	make -C src/test
	java -jar $< -i src/test/output -o publish -fvel
test1: publish/md2html.jar
	java -jar $< -i src/test/output -o publish/conf1 -fel -c src/test/conf1.yml
test2: publish/md2html.jar
	java -jar $< -i src/test/output -o publish/conf2 -fel -c src/test/conf2.yml
publish/%: target/%
	mkdir -p $(@D)
	cp $< $@
gh:
	git add -A; git commit && git push;
