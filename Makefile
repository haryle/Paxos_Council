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
PORT ?= 12345
ID ?= 1
MIN ?= 1000
MAX ?= 5000
DELAY ?= 3000
MAX_ATTEMPT ?= 5
TIMEOUT ?= 1000

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

central_registry: compile_src
	@java $(LOGGING_FLAG) -cp $(JARFILES):$(OUTDIR) CentralRegistry -p $(PORT) -t $(TIMEOUT) -a $(MAX_ATTEMPT)

acceptor: compile_src
	@java $(LOGGING_FLAG) -cp $(JARFILES):$(OUTDIR) AcceptorCouncillor -p $(PORT) -id $(ID)

proposer: compile_src
	@java $(LOGGING_FLAG) -cp $(JARFILES):$(OUTDIR) ProposerCouncillor -p $(PORT) -id $(ID) -min $(MIN) -max $(MAX) -d $(DELAY)


.PHONY = clean
clean:
	rm -rf out
	rm -rf report
	clear