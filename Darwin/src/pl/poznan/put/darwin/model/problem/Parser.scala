package pl.poznan.put.darwin.model.problem
import scala.util.parsing.combinator._
import pl.poznan.put.darwin.model.Problem

object Parser {

  object ProblemParser extends JavaTokenParsers {

    def problem: Parser[Any] = rep(line)

    def line: Parser[ProblemElement] =
        (expr ~ ";") ^^ {case e ~ _ => e}

    def expr: Parser[ProblemElement] =
        variable ^^ {case e => e} |
        goal ^^ {case e => e} |
        constraint ^^ {case e => e}
    
    def variable: Parser[VariableDef] =
        ("var" ~ "[" ~ floatingPointNumber ~ "," ~ floatingPointNumber ~ "]" ~ ident) ^^ {
          case _ ~ _ ~ min ~ _ ~ max ~ _ ~ name => VariableDef(name, min.toDouble, max.toDouble)
        }

    def goal: Parser[Goal] =
        (("min" | "max") ~ ident ~ ":" ~ math) ^^ {
          case max ~ name ~ _ ~ e => Goal(name, e, max == "max")
        }

    def constraint: Parser[Constraint] =
        (ident ~ ":" ~ math ~ (">=" | "<=") ~ math) ^^ {
           case name ~ _ ~ lhs ~ gte ~ rhs => Constraint(name, lhs, rhs, gte == ">=")
        }

    def math: Parser[Expr] =
        (term ~ ("+" | "-") ~ math) ^^ {
          case lhs ~ "+" ~ rhs => BinaryOp("+", lhs, rhs)
          case lhs ~ "-" ~ rhs => BinaryOp("-", lhs, rhs)
        } |
        term ^^ {case e => e}


    def term: Parser[Expr] = 
        (factor ~ ("*" | "/") ~ term) ^^ {
          case lhs ~ "*" ~ rhs => BinaryOp("*", lhs, rhs)
          case lhs ~ "/" ~ rhs => BinaryOp("/", lhs, rhs)
        } |
        factor ^^ {case e => e}

    def factor: Parser[Expr] =
        floatingPointNumber ^^ {case x => Constant(x.toDouble)} |
        aggregate ^^ {case e => e} |
        ((("+" | "-")) ~ factor) ^^ {
          case "+" ~ e => e
          case "-" ~ e => UnaryOp("-", e)
        } |
        ident ^^ {case name => Variable(name)} |
        "(" ~ math ~ ")"  ^^ {case _ ~ e ~ _ => e}

    def aggregate: Parser[AggregateOp] =
        (ident ~ "(" ~ repsep(math, ",") ~ ")") ^^ {
          case "min" ~ _ ~ ll ~ _ => AggregateOp("min", ll)
        }

    def parse(text: String) = {
      parseAll(problem, text)
    }

  }

}