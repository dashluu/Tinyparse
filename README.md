# Tinyparse

## About the project

I have always wanted to understand what happens behind the scene every time I type my code in C, Python, Java, and
many other programming languages. The only way to do so is to build a small compiler(OK but maybe not from scratch :)).
The fun of engineering is to construct things and see how far they can take you. That is exactly what this project is
about. It reflects my journey in learning about compilers. Although my knowledge on the topic is still shallow, I will
continue to update it, and any feedback will be much appreciated.

## References

* My book of
  choice: [The Dragon Book](https://www.amazon.com/Compilers-Principles-Techniques-Tools-2nd/dp/0321486811).
* I also borrowed some ideas and read a lot of code from these excellent resources:
    * [DoctorWkt on Github](https://github.com/DoctorWkt/acwj)
    * [Bob Nystrom's blog on Pratt's Parser](https://journal.stuffwithstuff.com/2011/03/19/pratt-parsers-expression-parsing-made-easy/)
    * [Matklad's Pratt parser implementation in Rust](https://matklad.github.io/2020/04/13/simple-but-powerful-pratt-parsing.html)
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

* **CharBuffer**: has an internal buffer which holds characters read from a stream (usually a file).
* **Lexer**:
    * Has an instance of CharBuffer to extract tokens from the input stream.
    * Identifies each token's type using the three types of tables below.

### Keyword table

* The code for the keyword table is in the package `Keywords`.
* The table is handcoded so values are predetermined.
* It stores all keywords that have been reserved for the language.

### Operator table

* The code for the operator table is in the package `Operators`.
* Similar to the keyword table, it is also handcoded.
* It is a collection of tables that store operators and their properties: one for prefix operators, one for infix 
operators, one for postfix operators, one for precedences, and one for associativities.

### Type table

* The code for the type table is in the package `Types`.
* Like other tables, it is handcoded.
* It stores all built-in data types and their sizes.

### Symbol tables

* The code for symbol tables is in the package `Symbols`.
* A symbol table stores relevant information about variables, functions, etc.
* One symbol table is created for each block, or scope, of code.
* The symbol tables are chained and accessed as stack-like objects so when a block of code has been processed by the
  parser, the system deallocates the most recently symbol table.

### Parser

#### Introduction and informal definitions

* **Context-free grammar**: fill out details later.
* **Leftmost derivation**: fill out details later.
* **Left recursion**: fill out details later.

#### Overview

For simplicity, our parser is a top-down parser with recursive descent and at most one lookahead. We need grammar
to define the correct syntax for our parser. However, writing our own grammar from scratch is extremely difficult and
time-consuming. To speed up the parser development, I decided to use some parts of existing languages' grammar (inspired
mostly by Kotlin and Swift). From that, I made some changes to make it even simpler since I am not trying to build a
full compiler!

#### Regex notations

I borrowed the following regular expression notations
from [Kotlin's grammar](https://kotlinlang.org/docs/reference/grammar.html).

* `|`: alternative.
* `*`: zero or more.
* `+`: one or more.
* `?`: zero or more

#### Token grammar

* **ID**: (`_` | (`a`-`z`) | (`A`-`Z`))(`_` | (`a`-`z`) | (`A`-`Z`) | (`0`-`9`))*
* **Digits**: (`0`-`9`)+
* **Number**: (`0`-`9`)+ | (`0`-`9`)\*`.`(`0`-`9`)\*
* **Scientific number**: ((`0`-`9`)+ | (`0`-`9`)\*`.`(`0`-`9`)\*)(`.` `e` (`+` | `-`)?((`0`-`9`)+ | (`0`-`9`)\*`.`(`0`-`9`)\*))?

#### Expression grammar

There are four types of expressions: primary, prefix, infix, and postfix. The following is the grammar for different
parts of an expression, which terminates once `;` is encountered.

```
expression -> infix-expression
primary-expression -> identifier | literal-expression | parenthesized-expression
parenthesized-expression -> '(' expression ')'
prefix-expression -> prefix-operator* postfix-expression
postfix-expression -> primary-expression postfix-operator
infix-expression -> prefix-expression infix-operator infix-expression
```

#### Declaration grammar

```
declaration -> variable-declaration
variable-declaration -> variable-declaration-head variable-name type-annotation initializer
variable-declaration-head -> 'var'
type-annotation -> ':' type
initializer -> '=' expression
```