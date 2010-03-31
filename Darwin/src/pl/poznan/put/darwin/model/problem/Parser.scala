package pl.poznan.put.darwin.model.problem
import scala.util.parsing.combinator._

object Parser {

  object ProblemParser extends JavaTokenParsers {

    def problem: Parser[Any] = rep(line)
    def line: Parser[Any] = expr ~ ";"
    def expr: Parser[Any] = variable | goal | constraint
    def variable: Parser[Any] = "var" ~ ident
    def goal: Parser[Any] = ident ~ ident ~ ":" ~ math
    def constraint: Parser[Any] = ident ~ ":" ~ math ~ (">=" | "<=") ~ math

    def math: Parser[Any] = term ~ rep("+" ~ term | "-" ~ term)
    def term: Parser[Any] = factor ~ rep("*" ~ factor | "/" ~ factor)
    def factor: Parser[Any] = floatingPointNumber | aggregate | ident | "(" ~ math ~ ")"
    def aggregate: Parser[Any] = ident ~ "(" ~ repsep(math, ",") ~ ")"

    def parse(text: String) = {
      parseAll(problem, text)
    }

  }

}