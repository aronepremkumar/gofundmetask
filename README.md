# GoFundMe Recurring Campaigns Processor

## Overview
This project takes input and processes GoFundMe recurring campaigns.

## Repository
The source code is available at: [https://github.com/aronepremkumar/gofundmetask](https://github.com/aronepremkumar/gofundmetask)

## Installation
To set up the project, you'll need to install GraalVM, create a JAR file, and generate an executable. For detailed instructions, see [INSTALL.md](INSTALL.md).

## Usage
1. Place the input file (`input.txt`) in the same directory as the executable file (`gfm-recurring`).
2. Run the program using one of the following commands:
   ```shell
   cat input.txt | ./gfm-recurring
   ```
   or
   ```shell
   ./gfm-recurring input.txt
   ```
