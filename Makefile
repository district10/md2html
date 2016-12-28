all: target/md2html.jar
target/md2html.jar: $(wildcard src/main/java/com/tangzhixiong/md2html/*.java)
	mvn package
	rm publish/ -rf
clean:
	rm -rf publish/ target/
test: target/md2html.jar
	java -jar $< -i demo/ -o publish

gh:
	git add -A; git commit -m "deal"; git push;