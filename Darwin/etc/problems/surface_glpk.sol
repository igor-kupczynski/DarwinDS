Problem:    surface_glpk
Rows:       4
Columns:    3
Non-zeros:  10
Status:     OPTIMAL
Objective:  dec = 1.1 (MINimum)

   No.   Row name   St   Activity     Lower bound   Upper bound    Marginal
------ ------------ -- ------------- ------------- ------------- -------------
     1 dec          B            1.1                             
     2 c1           B           -0.1          -0.6               
     3 c2           B            1.1           0.5               
     4 c3           NL           1.1           1.1                           1 

   No. Column name  St   Activity     Lower bound   Upper bound    Marginal
------ ------------ -- ------------- ------------- ------------- -------------
     1 x            B            0.1             0             1 
     2 y            NL             0             0             1             1 
     3 z            NU             1             0             1         < eps

Karush-Kuhn-Tucker optimality conditions:

KKT.PE: max.abs.err = 0.00e+00 on row 0
        max.rel.err = 0.00e+00 on row 0
        High quality

KKT.PB: max.abs.err = 0.00e+00 on row 0
        max.rel.err = 0.00e+00 on row 0
        High quality

KKT.DE: max.abs.err = 0.00e+00 on column 0
        max.rel.err = 0.00e+00 on column 0
        High quality

KKT.DB: max.abs.err = 0.00e+00 on row 0
        max.rel.err = 0.00e+00 on row 0
        High quality

End of output
