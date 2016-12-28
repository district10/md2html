inputs_txt := $(wildcard input/*.txt input/*/*.txt input/*/*/*.txt)
inputs := $(inputs_txt:%.txt=%)
outputs := $(inputs:input/%=output/%)

all: $(outputs)
output/%: input/%.txt
	@mkdir -p $(@D)
	perl ../../bin/cat.pl $< > $@

clean:
	@rm -rf output/
