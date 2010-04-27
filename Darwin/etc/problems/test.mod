var[0,10] x1; var[0,5] x2;

min time: x1 + 2*x2;
max profit: x1 + 3*x2;

market_limit_1: x1 <= 10;
market_limit_2: x2 <= 5;
material_limit_3: x1 + 2*x2 <= 15;
