<?php
    include_once("database.php");
    $conexao = getDatabaseConnection();
    $statement = $conexao->prepare("DELETE FROM tbl_filme WHERE id = ?");
    $statement->bind_param("i", $_GET["id"]);
    if ($statement->execute()) {
        $sucesso = true;
    } else {
        $sucesso = false;
    }

    $statement->close();
    $conexao->close();
    echo(json_encode(array("sucesso" => $sucesso)));
?>