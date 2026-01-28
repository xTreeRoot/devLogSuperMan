package org.treeroot.devlog.components

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonArray
import com.google.gson.JsonParser
import com.google.gson.JsonPrimitive

@Composable
fun JsonTreeView(
    jsonString: String,
    modifier: Modifier = Modifier
) {
    val jsonElement = remember(jsonString) {
        try {
            JsonParser.parseString(jsonString)
        } catch (_: Exception) {
            null
        }
    }

    if (jsonElement != null) {
        val scrollState = rememberLazyListState()
        
        Box(modifier = modifier.fillMaxSize()) {
            LazyColumn(
                state = scrollState,
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                item {
                    JsonTreeItem(
                        key = "",
                        value = jsonElement,
                        isRoot = true,
                        depth = 0
                    )
                }
            }
            
            VerticalScrollbar(
                adapter = rememberScrollbarAdapter(scrollState),
                modifier = Modifier.align(Alignment.TopEnd).fillMaxHeight()
            )
        }
    } else {
        Text("无效的JSON格式", color = MaterialTheme.colorScheme.error)
    }
}

@Composable
private fun JsonTreeItem(
    key: String,
    value: JsonElement,
    isRoot: Boolean = false,
    depth: Int,
    modifier: Modifier = Modifier
) {
    val indent = 16.dp * depth
    
    if (isRoot) {
        if (value.isJsonObject) {
            JsonObjectView(value.asJsonObject, depth)
        } else if (value.isJsonArray) {
            JsonArrayView(value.asJsonArray, depth)
        } else {
            JsonPrimitiveView(key, value.asJsonPrimitive, depth)
        }
    } else {
        Column(modifier = modifier.fillMaxWidth()) {
            if (value.isJsonObject) {
                ExpandableJsonObjectView(key, value.asJsonObject, depth)
            } else if (value.isJsonArray) {
                ExpandableJsonArrayView(key, value.asJsonArray, depth)
            } else {
                JsonPrimitiveView(key, value.asJsonPrimitive, depth)
            }
        }
    }
}

@Composable
private fun JsonObjectView(obj: JsonObject, depth: Int) {
    val indent = 16.dp * depth
    val keys = obj.keySet().toList()
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = indent),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = "{",
            fontFamily = FontFamily.Monospace,
            color = MaterialTheme.colorScheme.primary
        )
        
        keys.forEach { key ->
            val value = obj.get(key)
            JsonTreeItem(
                key = key,
                value = value!!,
                depth = depth + 1
            )
        }
        
        Text(
            text = "}",
            fontFamily = FontFamily.Monospace,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun ExpandableJsonObjectView(key: String, obj: JsonObject, depth: Int) {
    val indent = 16.dp * depth
    var expanded by remember { mutableStateOf(false) }
    val keys = obj.keySet().toList()
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = indent),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded },
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { expanded = !expanded },
                modifier = Modifier.size(20.dp)
            ) {
//                Icon(
//                    imageVector = Icons.Default.ExpandMore,
//                    contentDescription = if (expanded) "Collapse" else "Expand",
//                    modifier = Modifier
//                        .rotate(if (expanded) 90f else 0f)
//                        .size(16.dp)
//                )
            }
            
            Text(
                text = "$key: {",
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Text(
                text = " ${keys.size} items",
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelSmall
            )
            
            Text(
                text = if (expanded) "" else "... }",
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.primary
            )
        }
        
        if (expanded) {
            keys.forEach { childKey ->
                val value = obj.get(childKey)
                JsonTreeItem(
                    key = childKey,
                    value = value!!,
                    depth = depth + 1
                )
            }
            
            Text(
                text = "}",
                modifier = Modifier.padding(start = 16.dp),
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun JsonArrayView(array: JsonArray, depth: Int) {
    val indent = 16.dp * depth
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = indent),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = "[",
            fontFamily = FontFamily.Monospace,
            color = MaterialTheme.colorScheme.primary
        )
        
        array.forEachIndexed { index, element ->
            JsonTreeItem(
                key = "[$index]",
                value = element,
                depth = depth + 1
            )
        }
        
        Text(
            text = "]",
            fontFamily = FontFamily.Monospace,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun ExpandableJsonArrayView(key: String, array: JsonArray, depth: Int) {
    val indent = 16.dp * depth
    var expanded by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = indent),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded },
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { expanded = !expanded },
                modifier = Modifier.size(20.dp)
            ) {
//                Icon(
//                    imageVector = Icons.Default.ExpandMore,
//                    contentDescription = if (expanded) "Collapse" else "Expand",
//                    modifier = Modifier
//                        .rotate(if (expanded) 90f else 0f)
//                        .size(16.dp)
//                )
            }
            
            Text(
                text = "$key: [",
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Text(
                text = " ${array.size()} items",
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelSmall
            )
            
            Text(
                text = if (expanded) "" else "... ]",
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.primary
            )
        }
        
        if (expanded) {
            array.forEachIndexed { index, element ->
                JsonTreeItem(
                    key = "[$index]",
                    value = element,
                    depth = depth + 1
                )
            }
            
            Text(
                text = "]",
                modifier = Modifier.padding(start = 16.dp),
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun JsonPrimitiveView(key: String, primitive: JsonPrimitive, depth: Int) {
    val indent = 16.dp * depth
    val displayValue = if (primitive.isString) {
        "\"${primitive.asString}\""
    } else {
        primitive.toString()
    }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = indent),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (key.isNotEmpty()) {
            Text(
                text = "$key: ",
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        
        Text(
            text = displayValue,
            fontFamily = FontFamily.Monospace,
            color = when {
                primitive.isBoolean -> MaterialTheme.colorScheme.secondary
                primitive.isNumber -> MaterialTheme.colorScheme.tertiary
                primitive.isString -> MaterialTheme.colorScheme.primary
                else -> MaterialTheme.colorScheme.onSurface
            }
        )
    }
}