BUILD_DIR ?= build
SOURCES := $(shell find src -name '*.java' | sort)
TEST_SOURCES := $(shell find tests -name '*.java' | sort)

.PHONY: all test clean

all:
	mkdir -p $(BUILD_DIR)
	javac -Xlint:all -Werror -d $(BUILD_DIR) $(SOURCES)

test: all
	javac -Xlint:all -Werror -cp $(BUILD_DIR) -d $(BUILD_DIR) $(TEST_SOURCES)
	java -cp $(BUILD_DIR) RunHw5Checks
	java -cp $(BUILD_DIR) il.ac.tau.cs.sw1.ex5.BigramModelTester

clean:
	rm -rf $(BUILD_DIR)
	rm -f resources/hw5/all_you_need_model.voc resources/hw5/all_you_need_model.counts
