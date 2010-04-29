package pl.poznan.put.darwin.model.problem
import scala.util.parsing.combinator._

object Parser {

  def fromText(text: String): Problem = {
    val result: ProblemParser.ParseResult[Problem] = ProblemParser.parse(text)
    result.get
  }

  object ProblemParser extends JavaTokenParsers {

    def problem: Parser[Problem] =
        rep(line) ^^ { case ll => Problem(ll) }

    def line: Parser[ProblemElement] =
        (expr ~ ";") ^^ {case e ~ _ => e}

    def expr: Parser[ProblemElement] =
        utilityFunc ^^ {case e => e} |
        variable ^^ {case e => e} |
        goal ^^ {case e => e} |
        constraint ^^ {case e => e}
    
    def variable: Parser[VariableDef] =
        ("var" ~ "[" ~ additionalConstraint ~ floatingPointNumber ~ "," ~ floatingPointNumber ~ "]" ~ ident) ^^ {
          case _ ~ _ ~ addConstraint ~ min ~ _ ~ max ~ _ ~ name =>
            VariableDef(name, min.toDouble, max.toDouble, addConstraint)
        }

    def additionalConstraint: Parser[AdditionalConstraint] = {
      "(B)" ^^ { case _ => BinaryConstraint }  |
      "(I)" ^^ { case _ => IntegerConstraint } |
      "" ^^ { case _ => null }
    }
  
    def goal: Parser[Goal] =
        (("min" | "max") ~ ident ~ ":" ~ math) ^^ {
          case max ~ name ~ _ ~ e => Goal(name, e, max == "max")
        }

    def constraint: Parser[Constraint] =
        (ident ~ ":" ~ math ~ (">=" | "<=") ~ math) ^^ {
           case name ~ _ ~ lhs ~ gte ~ rhs => Constraint(name, lhs, rhs, gte == ">=")
        }

    def utilityFunc: Parser[UtilityFunction] =
        ("!dec:" ~ math) ^^ {
          case _ ~ e => UtilityFunction(e)
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
        interval ^^ {case e => e} |
        "(" ~ math ~ ")"  ^^ {case _ ~ e ~ _ => e}

    def interval: Parser[Expr] = {
      "[" ~ ident ~ ":" ~ floatingPointNumber ~ "," ~ floatingPointNumber ~ "]" ^^ {
        case _ ~ name ~ _ ~ lower ~ _ ~ upper ~ _ =>
          Interval(name, lower.toDouble, upper.toDouble) }
    }
  
    def aggregate: Parser[AggregateOp] =
        (ident ~ "(" ~ repsep(math, ",") ~ ")") ^^ {
          case "min" ~ _ ~ ll ~ _ => AggregateOp("min", ll)
        }

    def parse(text: String) = {
      parseAll(problem, text)
    }

  }
}
