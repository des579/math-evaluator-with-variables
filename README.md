# Mathematical Expression Evaluator

A mathematical expression evaluator written in Java that allows users to evaluate mathematical expressions, perform arithmetic operations, and use variables to store and reuse values.

## Features

- **Arithmetic operations:** Addition (`+`), subtraction (`-`), multiplication (`*`), and division (`/`).
- **Variables:** Define variables and assign them numeric values.
- **Variable reuse:** Use previously defined variables in mathematical expressions.
- **Expression evaluation:** Evaluate expressions containing numbers, variables, and operators.
- **Variable management:** Store, update, and clear variables.
- **Error handling:** Handle invalid expressions, undefined variables, and invalid numerical operations.

## Run the project 

Compile the source files:

```bash
javac -d out src/*.java
```

Run the application:

```bash
java -cp out Main
```

## Example Usage

```text
> x = 10
10.0

> y = 5
5.0

> x + y
15.0

> x * y
50.0

> z = x / y
2.0

> z + 10
12.0
```

## Supported Operations

| Operator | Description | Example |
|---|---|---|
| `+` | Addition | `5 + 3 = 8` |
| `-` | Subtraction | `5 - 3 = 2` |
| `*` | Multiplication | `5 * 3 = 15` |
| `/` | Division | `6 / 3 = 2` |

## Variables

Variables can be assigned numerical values and reused in subsequent expressions.

```text
a = 10
b = 20
c = a + b
```

Variables can also be reassigned:

```text
a = 15
a + b
35.0
```

## Error Handling

The evaluator handles common errors, including:

- Invalid mathematical expressions.
- Usage of undefined variables.
- Division by zero.
- Invalid variable assignments.
- Invalid numerical input.

## Technologies

- **Language:** Java
- **Data types:** `Double`
- **Variable storage:** `Hashtable`

## Getting Started

1. Clone or download the repository.
2. Open the project in your preferred Java IDE.
3. Compile and run the application.
4. Enter mathematical expressions and variable assignments.

## Example

```text
> price = 12.5
12.5

> quantity = 4
4.0

> total = price * quantity
50.0

> total + 10
60.0
```

## License

This project is licensed under MIT License.
