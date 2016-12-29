inputs_txt := $(wildcard input/*.txt input/*/*.txt input/*/*/*.txt)
inputs := $(inputs_txt:%.txt=%)
outputs := $(inputs:input/%=output/%)

inputs_md := $(wildcard input/*.md input/*/*.md input/*/*/*.md)

all: $(outputs) $(inputs_md:input/%=output/%) output/.md2html.yml
	echo $(outputs)
output/%: input/%.txt
	@mkdir -p $(@D)
	perl ../../bin/cat.pl $< > $@
output/%: input/%
	@mkdir -p $(@D)
	cp $< $@

clean:
	@rm -rf output/
