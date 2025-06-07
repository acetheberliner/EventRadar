<?php
header('Content-Type: application/json');

$mysqli = new mysqli("localhost", "root", "", "eventradar");

if ($mysqli->connect_error) {
    http_response_code(500);
    echo json_encode(["error" => "Connessione fallita"]);
    exit();
}

$result = $mysqli->query("SELECT * FROM events");

$events = [];
while ($row = $result->fetch_assoc()) {
    $row['imageUrl'] = $row['imageUrl'] ?: 'https://via.placeholder.com/150';
    $events[] = $row;
}

echo json_encode(["events" => $events]);
?>
