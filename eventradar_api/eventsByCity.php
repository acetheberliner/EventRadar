<?php
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json");

require_once 'db.php';

$city = $_GET['city'] ?? '';

$stmt = $pdo->prepare("SELECT * FROM events WHERE TRIM(location) = ?");
$stmt->execute([$city]);

$results = $stmt->fetchAll(PDO::FETCH_ASSOC);

echo json_encode(["events" => $results]);
?>
