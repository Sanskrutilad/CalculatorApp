package com.example.calculatorapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.calculatorapp.ui.theme.CalculatorappTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalculatorappTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CalculatorScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun CalculatorScreen(modifier: Modifier = Modifier) {
    var result by remember { mutableStateOf("0") }
    var input by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Display area
        Text(
            text = input.ifEmpty { result },
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.fillMaxWidth()
        )

        // Buttons
        Column {
            // Button row for numbers
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                NumberButton("7") { input += "7" }
                NumberButton("8") { input += "8" }
                NumberButton("9") { input += "9" }
                OperationButton("/") { input += " / " }
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                NumberButton("4") { input += "4" }
                NumberButton("5") { input += "5" }
                NumberButton("6") { input += "6" }
                OperationButton("*") { input += " * " }
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                NumberButton("1") { input += "1" }
                NumberButton("2") { input += "2" }
                NumberButton("3") { input += "3" }
                OperationButton("-") { input += " - " }
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                NumberButton("0") { input += "0" }
                Button(onClick = {
                    result = evaluateExpression(input.trim()) // Trim the input to remove trailing spaces
                    input = ""
                }) {
                    Text("=", modifier = Modifier.padding(16.dp))
                }
                OperationButton("+") { input += " + " }
            }
        }
    }
}

@Composable
fun NumberButton(number: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.padding(4.dp)
    ) {
        Text(number)
    }
}

@Composable
fun OperationButton(operation: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.padding(4.dp)
    ) {
        Text(operation)
    }
}

fun evaluateExpression(expression: String): String {
    val tokens = expression.split(" ").filter { it.isNotEmpty() }
    if (tokens.isEmpty()) return "Error"

    val numbers = mutableListOf<Double>()
    val operators = mutableListOf<String>()
    for (token in tokens) {
        if (token.toDoubleOrNull() != null) {
            numbers.add(token.toDouble())
        } else if (token in listOf("+", "-", "*", "/")) {
            operators.add(token)
        } else {
            return "Error"
        }
    }
    if (numbers.size != operators.size + 1) return "Error"
    val newNumbers = mutableListOf<Double>()
    val newOperators = mutableListOf<String>()

    newNumbers.add(numbers[0])

    for (i in operators.indices) {
        val operator = operators[i]
        val number = numbers[i + 1]

        if (operator == "*" || operator == "/") {
            val lastNumber = newNumbers.removeAt(newNumbers.lastIndex)
            val result = when (operator) {
                "*" -> lastNumber * number
                "/" -> if (number != 0.0) lastNumber / number else return "Error" // Handle division by zero
                else -> lastNumber
            }
            newNumbers.add(result)
        } else {
            newNumbers.add(number)
            newOperators.add(operator)
        }
    }
    var total = newNumbers[0]
    for (i in newOperators.indices) {
        total = when (newOperators[i]) {
            "+" -> total + newNumbers[i + 1]
            "-" -> total - newNumbers[i + 1]
            else -> total
        }
    }

    return total.toString()
}

@Preview(showBackground = true)
@Composable
fun CalculatorPreview() {
    CalculatorappTheme {
        CalculatorScreen()
    }
}
