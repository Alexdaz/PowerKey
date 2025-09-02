package net.alex.powerkey

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import net.alex.powerkey.ui.theme.PowerKeyTheme

private const val MIN_LENGTH = 12
private const val MAX_LENGTH = 32

class MainActivity : ComponentActivity() {

    companion object {
        init {
            System.loadLibrary("native-lib")
        }
    }

    //JNI
    external fun generateRandomPassword(
        length: Int,
        useUpper: Boolean,
        useLower: Boolean,
        useDigits: Boolean,
        useSpecial: Boolean
    ): String

    external fun generateMemorablePassword(
        length: Int,
        useUpper: Boolean,
        useLower: Boolean,
        useDigits: Boolean,
        useSpecial: Boolean
    ): String
    external fun generateEasyPassword(): String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PowerKeyTheme {

                val navBarScrimColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)

                SideEffect {
                    enableEdgeToEdge(
                        statusBarStyle = SystemBarStyle.auto(
                            lightScrim = navBarScrimColor.toArgb(),
                            darkScrim = navBarScrimColor.toArgb() ),
                        navigationBarStyle = SystemBarStyle.auto(
                            lightScrim = navBarScrimColor.toArgb(),
                            darkScrim = navBarScrimColor.toArgb() ) )
                }

                Surface(Modifier.fillMaxSize()) {
                    Box(Modifier.fillMaxSize()) {
                        PasswordGeneratorScreen(
                            onGenerateRandom = { len, up, low, dig, spec ->
                                generateRandomPassword(len, up, low, dig, spec)
                            },
                            onGenerateMemorable = { len, up, low, dig, spec ->
                                generateMemorablePassword(len, up, low, dig, spec)
                            },
                            onGenerateEasy = { generateEasyPassword() }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordGeneratorScreen(
    onGenerateRandom: (Int, Boolean, Boolean, Boolean, Boolean) -> String,
    onGenerateMemorable: (Int, Boolean, Boolean, Boolean, Boolean) -> String,
    onGenerateEasy: () -> String
)
{
    val context = LocalContext.current

    var length by rememberSaveable { mutableIntStateOf(MIN_LENGTH) }
    var useUpper by rememberSaveable { mutableStateOf(true) }
    var useLower by rememberSaveable { mutableStateOf(true) }
    var useDigits by rememberSaveable { mutableStateOf(true) }
    var useSpecial by rememberSaveable { mutableStateOf(false) }

    var memorable by rememberSaveable { mutableStateOf(false) }
    var easy by rememberSaveable { mutableStateOf(false) }

    var password by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(easy) { if (easy) memorable = false }
    LaunchedEffect(memorable) {
        if (memorable)
        {
            easy = false
            if (length > MIN_LENGTH) length = MIN_LENGTH
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets.systemBars
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                stringResource(id = R.string.app_name),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )

            Column(
                modifier = Modifier
                    .then(
                        if (easy) Modifier else Modifier
                    )
            ){
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(stringResource(id = R.string.length))
                    Text("$length")
                }

                val maxLength = if (memorable) MIN_LENGTH else MAX_LENGTH

                Slider(
                    value = length.toFloat().coerceIn(6f, maxLength.toFloat()),
                    onValueChange = { length = it.toInt() },
                    valueRange = 6f..maxLength.toFloat(),
                    steps = (maxLength - 6 - 1),
                    enabled = !easy
                )
            }

            ElevatedCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(12.dp)
                        .then(
                            if (easy) Modifier else Modifier
                        ),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(stringResource(id = R.string.char_options), fontWeight = FontWeight.Medium)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = useUpper,
                            onCheckedChange = { useUpper = it },
                            enabled = !easy
                        )
                        Text(stringResource(id = R.string.upper_case))
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = useLower,
                            onCheckedChange = { useLower = it },
                            enabled = !easy
                        )
                        Text(stringResource(id = R.string.lower_case))
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = useDigits,
                            onCheckedChange = { useDigits = it },
                            enabled = !easy
                        )
                        Text(stringResource(id = R.string.numbers))
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = useSpecial,
                            onCheckedChange = { useSpecial = it },
                            enabled = !easy
                        )
                        Text(stringResource(id = R.string.symbols))
                    }
                }
            }

            ElevatedCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(stringResource(id = R.string.password_types), fontWeight = FontWeight.Medium)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Switch(checked = memorable, onCheckedChange = { memorable = it })
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(id = R.string.memorable))
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Switch(checked = easy, onCheckedChange = { easy = it })
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(id = R.string.easy))
                    }
                }
            }

            Button(
                onClick = {
                    if (!easy && !useUpper && !useLower && !useDigits && !useSpecial) {
                        Toast.makeText(
                            context,
                            R.string.select,
                            Toast.LENGTH_SHORT
                        ).show()
                        return@Button
                    }

                    password = when {
                        easy -> onGenerateEasy()
                        memorable -> onGenerateMemorable(length, useUpper, useLower, useDigits, useSpecial)
                        else -> onGenerateRandom(length, useUpper, useLower, useDigits, useSpecial)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(id = R.string.generate))
            }

            val passwordLabel = context.getString(R.string.password)

            if (password.isNotEmpty())
            {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(stringResource(id = R.string.result), style = MaterialTheme.typography.titleMedium)
                    SelectionContainer {
                        Text(
                            password,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    OutlinedButton(
                        onClick = { copyToClipboard(context, passwordLabel, password) },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text(stringResource(id = R.string.copy)) }
                }
            }

            AssistiveNotes(easyEnabled = easy)
        }
    }
}

@Composable
private fun AssistiveNotes(easyEnabled: Boolean)
{
    if (easyEnabled)
    {
        Text(
            stringResource(id = R.string.easy_help),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
    else
    {
        Text(
            stringResource(id = R.string.advice),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun copyToClipboard(context: Context, label: String, text: String)
{
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText(label, text)
    clipboard.setPrimaryClip(clip)
}

@Preview(showBackground = true, name = "Preview for PowerKey")
@Composable
fun PasswordGeneratorScreenPreview()
{
    PowerKeyTheme{
        Surface(modifier = Modifier.fillMaxSize()) {
            PasswordGeneratorScreen(
                onGenerateRandom = { _, _, _, _, _ -> "MockRandom123!" },
                onGenerateMemorable = { _, _, _, _, _ -> "MockMemorablePass" },
                onGenerateEasy = { "AAA999aaa!" }
            )
        }
    }
}