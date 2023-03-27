# Tinyparse
## About the project
I have always wanted to implement a compiler from scratch. By doing so, I get to know
what happens behind the scene every time I type my code in C, Python, or Java. For references,
my book of choice is [Dragon Book](https://www.amazon.com/Compilers-Principles-Techniques-Tools-2nd/dp/0321486811).
Although my knowledge on compilers is still very shallow, this project assembles everything
I have learned, and it will continue to be updated.
## Computer system basics
As I learned in my C programming class, there are several phases in the compilation process:
* **Preprocessing**: modifies source code by processing include statements, directives and macros.
* **Compiling**: compiles preprocessed source code to assembly.
* **Assembling**: turns assembly instructions into relocatable machine code.
* **Linking**: links relocatable machine code with code from other object files to produce executables.
## Compiler v.s Interpreter
* **Compiler**: compiles the source language to a target language.
* **Interpreter**: executes the source code to produce some output.
* **Hybrid**: as wrote in the Dragon Book(Compiler Bible), this combines a compiler with an interpreter.
For example, Java Virtual Machine(JVM) first compiles Java source code to an assembly-like language called
bytecodes. JVM's interpreter then executes bytecode instructions to produce some output.
## Compilation phases
* **Lexing (or tokenizing, aka lexical analysis)**: tokenizes the code and splits it into small units called
tokens which is composed of a type, a string value, and a line number that the token is on.
* **Parsing**: I divide parsing into two smaller phases.
  * **Syntax analysis**: consumes the tokens and "stitches" them together by following some rules, or
    grammar. The result produced by parser is a syntax tree.
  * **Semantic analysis**: figures out what the code is trying to do. Some things to do in this phase are
type checking and type conversion.
## Components
### Lexer


