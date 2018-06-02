<?php
    include_once("database.php");
    $conexao = getDatabaseConnection();
    if (!isset($_GET["id"])) {
        $filmes = [];
        $result = $conexao->query("SELECT * FROM tbl_filme");
        while ($data = $result->fetch_assoc()) {
            $filmes[] = $data;
        }

        echo json_encode($filmes);
        
    } else {
        $statement = $conexao->prepare("SELECT * FROM tbl_filme WHERE id = ?");
        $statement->bind_param("i", $_GET["id"]);
        $statement->execute();
        $result = $statement->get_result();
        if ($data = $result->fetch_assoc()) {
            echo json_encode($data);
        }
    }

    $conexao->close();
?>