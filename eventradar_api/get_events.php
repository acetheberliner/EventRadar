<?php
header('Content-Type: application/json');

$mysqli = new mysqli("localhost", "root", "", "eventradar");

if ($mysqli->connect_error) {
    http_response_code(500);
    echo json_encode(["error" => "Database connection failed"]);
    exit();
}

$city = $_GET['city'] ?? '';
$date = $_GET['date'] ?? '';

if (!$city || !$date) {
    http_response_code(400);
    echo json_encode(["error" => "Missing parameters"]);
    exit();
}

$stmt = $mysqli->prepare("SELECT * FROM events WHERE location = ? AND date = ?");
$stmt->bind_param("ss", $city, $date);
$stmt->execute();
$result = $stmt->get_result();

$events = [];
while ($row = $result->fetch_assoc()) {
    $events[] = $row;
}

echo json_encode(["events" => $events]);
?>
