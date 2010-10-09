var[0, 13] xa;
var[0, 25] xb;
var[0, 13] xc;

max profit: [pa:20, 24] * min(xa, [da:10, 12])
 + [pb:30, 36] * min(xb, [db:20, 24])
 + [pc:25, 30] * min(xc, [dc:10, 12])
 - [p1r:6, 7.2] * ([r1a:1,   1.2]*xa + [r1b:2, 2.4]*xb + [r1c:0.75, 0.9]*xc)
 - [p2r:9, 9.6] * ([r2a:0.5, 0.6]*xa + [r2b:1, 1.2]*xb + [r2c:0.5,  0.6]*xc);

min time: [ta:5, 6]*xa + [tb:8, 9.6]*xb + [tc:10, 12]*xc;

c1: xa <= 12;
c2: xb <= 24;
c3: xc <= 12;
c4: xa >= 0;
c5: xb >= 0;
c6: xc >= 0;

!dec:      <profit, 0.01> + 3 * <profit, 0.25> + 2 * <profit, 0.50>
      -1 * <time, 0.01>   - 3 * <time, 0.25>   - 2 * <time, 0.50>;
