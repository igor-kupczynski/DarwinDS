var x >=0 <=1;
var y >=0 <=1;
var z >=0 <=1;

minimize dec: 1.000000 * x + 2.000000 * y  + 1.000000 * z;

s.t. c1: -x + y + 0.6 >= 0;
s.t. c2: x + z - 0.5 >= 0;
s.t. c3: x + y + z - 1.1 >= 0;

end;
