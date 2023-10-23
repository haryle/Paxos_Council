OUTDIR = out/production/PaxosCouncil
TESTOUTDIR = out/tests
CLASSDIR = src
UTILDIR = src/utility
TESTDIR  = tests/
JUNITJAR = junit-platform-console-standalone-1.9.3.jar
JAVATUPLEJAR = javatuples-1.2.jar
JARDIR = jar
JARFILES = $(JARDIR)/$(JAVATUPLEJAR):$(JARDIR)/$(JUNITJAR)
SHELL := /usr/bin/bash
LOGGING_FLAG = -Djava.util.logging.config.file=config/logging.properties

make_dirs:
	@mkdir -p $(OUTDIR) $(TESTOUTDIR)

compile_src: make_dirs
	@find $(CLASSDIR) -name "*.java" > sources.txt
	javac -d $(OUTDIR) -cp $(JARFILES):$(CLASSDIR) @sources.txt
	@rm sources.txt

compile_test: make_dirs
	@find $(TESTDIR) -name "*.java" > sources.txt
	@javac -d $(TESTOUTDIR) -cp $(JARFILES):$(CLASSDIR) @sources.txt
	@rm sources.txt

run_test: compile_src compile_test
	@java $(LOGGING_FLAG) -javaagent:jar/intellij-coverage-agent-1.0.737.jar=config/config.args -jar $(JARDIR)/$(JUNITJAR) -cp $(JARFILES):$(TESTOUTDIR):$(OUTDIR) --scan-classpath


.PHONY = clean
clean:
	rm -rf out
	rm -rf report
	clear