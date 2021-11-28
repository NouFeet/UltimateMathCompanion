/*
 * Copyright 2021 Nikita Kuprins
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.ultimatemathcompanion.math;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class Calculations {

    // Valid format is: 2 / 3 + 1 * 4
    // No redundant spaces or unclear symbols.
    // All numbers are only integers.
    private static final Pattern validFormat = Pattern.compile("^-?\\d+( [+\\-*/] -?\\d+)+$");
    private static final List<Character> mathSigns = Arrays.asList('+', '-', '*', 'x', '/', '÷');

    private enum Types {
        PlusMinus(1),     // +-
        DivMultipl(3),    // */
        Combined(2),      // */+-
        LongExpression(4);  // */+-*/+-

        private final int id;

        Types(int id) {
            this.id = id;
        }
    }

    private Calculations() {}

    public static long countDigits(String str) {
        return str.chars().filter(Character::isDigit).count();
    }

    public static Set<Character> getExpressionSigns(String str) {
        return Arrays.stream(str.split(" "))
                .filter(s -> s.length() == 1)
                .map(s -> s.charAt(0))
                .filter(mathSigns::contains)
                .collect(Collectors.toSet());
    }

    public static int getExpressionTypeId(String str) {
        if (countDigits(str) >= 30)
            return Types.LongExpression.id;

        Set<Character> signs = getExpressionSigns(str);

        if (!signs.contains('/') && !signs.contains('*'))
            return Types.PlusMinus.id;

        if (!signs.contains('+') && !signs.contains('-'))
            return Types.DivMultipl.id;

        return Types.Combined.id;
    }

    public static boolean isValidFormat(String str) {
        Matcher matcher = validFormat.matcher(str);
        return matcher.matches();
    }

    // Splits expression by math order
    // Example:
    // Input: 2 * 3 - 1 / 3 + 2
    // Output: [ 2 * 3, - , 1 / 3 , + , 2 ]
    //
    // Input: 2 * 3 / 5 * 7
    // Output: [ 2, * , 3 , / , 5 , * , 7 ]
    private static String[] splitByOperationsOrder(String expression) {
        Set<Character> signs = getExpressionSigns(expression);
        if (!signs.contains('+') && !signs.contains('-'))
            return expression.split(" ");

        return expression.split("(?<=[^/*])\\s(?=[^/*])");
    }

    public static BigDecimal calculate(String expression) {
        BigDecimal result = BigDecimal.ZERO;
        BigDecimal temp;
        String operation = "+";
        String[] expressionOrders = splitByOperationsOrder(expression);

        for (String s : expressionOrders) {
            if (s.length() == 1 && mathSigns.contains(s.charAt(0))) {
                // Variable s is an operation symbol
                operation = s;
                continue;
            }

            if (s.chars().anyMatch(item -> item == ' ')) {
                // Variable s is expression(with / or * operators. For example, 2*3/2)
                // Recursion of calculate() will not be forever, as splitByOperationsOrder() will split
                // new expression(2*3/2) by spaces, so it will definitely return BigDecimal.
                temp = calculate(s);
            } else {
                // Variable s is just number
                temp = new BigDecimal(s);
            }

            result = calculate(result, temp, operation);
        }

        return result;
    }

    public static BigDecimal calculate(BigDecimal num1, BigDecimal num2, String operation) {
        switch (operation) {
            case "+":
                return num1.add(num2);
            case "-":
                return num1.subtract(num2);
            case "x": case "*":
                return num1.multiply(num2);
            case "÷": case "/":
                return num1.divide(num2, 4, RoundingMode.HALF_UP);
            default:
                return BigDecimal.ZERO;
        }
    }
}
