# Tinyparse

## About the project

I have always wanted to understand what happens behind the scene every time I type my code in C, Python, Java, and
many other programming languages. The only way to do so is to build a small compiler(OK but maybe not from scratch :)).
The fun of engineering is to construct things and see how far they can take you. That is exactly what this project is
about. It reflects my journey in learning about compilers. Although my knowledge on the topic is still shallow, I will
continue to update it, and hopefully, it will inspire others to get started with compilers in the future.

## References

* My book of
  choice: [Bible on Compilers(the Dragon Book)](https://www.amazon.com/Compilers-Principles-Techniques-Tools-2nd/dp/0321486811).
* I also borrowed some ideas and read a lot of code from these resources:
    * [DoctorWkt on Github](https://github.com/DoctorWkt/acwj)
    * [Bob Nystrom's blog on Pratt's Parser](https://journal.stuffwithstuff.com/2011/03/19/pratt-parsers-expression-parsing-made-easy/)
* You cannot write a compiler without looking at some other compilers! So I chose the following list of compilers for
  references(mostly on grammar):
    * [C's grammar](https://learn.microsoft.com/en-us/cpp/c-language/c-language-syntax-summary?view=msvc-170)
    * [Swift's grammar](https://docs.swift.org/swift-book/documentation/the-swift-programming-language/summaryofthegrammar#app-top)
    * [Swift's compiler](https://www.swift.org/swift-compiler/)
    * [Kotlin's grammar](https://kotlinlang.org/docs/reference/grammar.html)

## Computer system basics

There are several phases in the compilation process:

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
* **Parsing**: We can divide parsing into two smaller phases.
    * **Syntax analysis**: consumes the tokens and "stitches" them together by following some rules, or
      grammar. The result produced by parser is an abstract syntax tree, or AST.
    * **Semantic analysis**: figures out what the code is trying to do. Some things to do in this phase are
      type checking and type conversion.

## Components

### Lexer

The code for lexer is put in the package `Lexer` in the repository. It includes:

* **CharBuffer**: the class that has an internal buffer which holds characters read from a stream (usually a file).
* **Lexer**: the class that has an instance of CharBuffer to extract tokens from the input stream
  and identifies each token's type. Lexer's two main methods are:
    * `peekToken`: peeks the next tokens in the stream.
    * `readToken`: extracts the next tokens from the stream.
* **LexerTable**: a table that maps each token's value to some reserved words, which includes "true", "false", "+", etc.

### Reserved table

* The code for the reserved table is in the package `Reserved`.
* It is a handcoded table which maps a token value to some reserved word.
* There are three types of reserved words: keyword, operator, and data type.

### Symbol tables

* The code for symbol tables is in the package `Symbols`.
* A symbol tables stores relevant information about variables, functions, etc.
* One symbol table is created for each block, or scope, of code.
* The symbol tables are chained and accessed as stack-like objects so when a block of code has been processed by the
  parser, the system frees the most recently symbol table.

### Parser

For simplicity, our parser uses LL(1), a type of top-down parser with recursive descent strategy and at most one
lookahead. Next, we need grammar to define the correct syntax for our parser. However, writing our own grammar from
scratch is extremely difficult and time-consuming. To speed up parser development, I decided to use Microsoft C's
grammar(see the reference list). From that, I made some changes to make it even simpler since I am not trying to build
a full compiler!