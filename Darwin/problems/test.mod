#
# Test problem for the parser
#


var x1; /* soldier */
var x2; /* train */


/* Objective function */
min time: x1 + 2*x2:
max profit: x1 + 3*x2;


/* Constraints */
market limit 1: x1 <= 10;
market limit 2: x2 <= 5;
material limit 3: x1 + 2*x2 <= 15;
