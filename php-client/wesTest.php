<!DOCTYPE html>
<html>
    <head>
        <title>Testing PHP client for Wikidata Entity Suggester</title>
    </head>
    <body>
<?php
require_once("vendor/autoload.php");

use WesPHPClient\EntitySuggesterService;

if (!isset($_POST['data'])) {
    ?>
    <h1>Enter data</h1>
    <form action="wesTest.php" method="post">
        Please enter the properties and property----value pairs in this text box. An example set is already given.<br/><br/>
        <textarea rows="10" cols="50" name="data">
107----4167410
106
107----215627
156
        </textarea><br/><br/>
        <input type="radio" name="t" value="value" checked>Suggest values
        <input type="radio" name="t" value="property">Suggest properties
        <br/><br/>
        <input type="submit" value="Get suggestions!"/>
    </form>

    <?php
    exit;
}
$wes = new EntitySuggesterService('127.0.0.1', 8080);
$type = isset($_POST['t']) ? $_POST['t'] : "property";
if (trim($_POST['data']) == '')
    die("You must provide some data.");
$data = array_map('trim', explode("\n", str_replace(" ", "", $_POST['data'])));

$results = $wes->getRecommendation($data, $type, 10);

mysql_connect("localhost", "root", "hackalot");
mysql_select_db("wikidatawiki") or die(mysql_error());

if ($type == "property") {
    echo "<h1>Suggested properties:</h1><br/>\n";
    $query = "SELECT pl_id, pl_text FROM plabel WHERE pl_lang='en' AND pl_id IN (";
    ?>
    <table border="1">
        <tr>
            <th>PropertyID</th>
            <th>Property Value</th>
        </tr>
    <?php
    foreach ($results as $result) {
        $query .= "$result[0],";
    }
    $query = substr($query, 0, -1) . ")";
    $mysqlResult = mysql_query($query) or die(mysql_error());
    while ($fetch = mysql_fetch_assoc($mysqlResult)) {
        ?>
            <tr>
                <td><?php echo $fetch['pl_id']; ?></td>
                <td><?php echo $fetch['pl_text']; ?></td>
            </tr>
        <?php
    }
} else {
    echo "<h1>Suggested property-value pairs:</h1><br/>\n";
    ?>
        <table border="1">
            <tr>
                <th>PropertyID</th>
                <th>Property</th>
                <th>ValueID</th>
                <th>Value</th>
            </tr>
    <?php
    $map = array();
    $query = "SELECT pl_id, pl_text FROM plabel WHERE pl_lang='en' AND pl_id IN (";
    foreach ($results as $result) {
        $result = explode("----", $result[0]);
        $query .= $result[0] . ",";
    }
    $query = substr($query, 0, -1) . ")";
    $mysqlResult = mysql_query($query) or die(mysql_error());
    while ($fetch = mysql_fetch_assoc($mysqlResult)) {
        $map[$fetch['pl_id']] = $fetch['pl_text'];
    }

    $query = "SELECT l_id, l_text FROM label WHERE l_lang='en' AND l_id=";
    foreach ($results as $result) {
        $result = explode("----", $result[0]);
        if (!isset($result[1])) {
            continue;
        } else {
            if (is_numeric($result[1])) {
                $mysqlResult = mysql_query($query . $result[1]) or die(mysql_error());
                while ($fetch = mysql_fetch_assoc($mysqlResult)) {
                    $valID = $fetch['l_id'];
                    $valText = $fetch['l_text'];
                }
                ?>
                        <tr>
                            <td><?php echo $result[0]; ?></td>
                            <td><?php echo $map[$result[0]]; ?></td>
                            <td><?php echo $valID; ?></td>
                            <td><?php echo $valText; ?></td>
                        </tr>
                <?php
            } else {
                ?>
                        <tr>
                            <td><?php echo $result[0]; ?></td>
                            <td><?php echo $map[$result[0]]; ?></td>
                            <td></td>
                            <td><?php echo $result[1]; ?></td>
                        </tr>
                <?php
            }
        }
    }
}
flush();
?>
        <a href="wesTest.php"> Go Back </a>
        </body>
        </html>