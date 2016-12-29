all: target/md2html.jar
target/md2html.jar: $(wildcard src/main/java/com/tangzhixiong/md2html/*.java) $(wildcard src/main/resources/*)
	mvn package
	rm publish/ -rf
clean:
	rm -rf publish/ target/
test: publish/md2html.jar
	make -C src/test
	java -jar $< -i src/test/output -o publish -fve
	java -jar $< -i src/test/output -o publish/conf1 -fe -c src/test/conf1.yml
	java -jar $< -i src/test/output -o publish/conf2 -fe -c src/test/conf2.yml
publish/%: target/%
	mkdir -p $(@D)
	cp $< $@
gh:
	git add -A; git commit && git push;
