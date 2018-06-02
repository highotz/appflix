<?php
    function getDatabaseConnection() {
        try {
            mysqli_report(MYSQLI_REPORT_STRICT);
            $conexao = new mysqli("127.0.0.1", "root", "bcd127", "db_apifilme");
            $conexao->autocommit(true);
            $conexao->set_charset("utf8");
            return $conexao;
        } catch (Exception $e) {
            die(json_encode(array("erro" => "Erro ao connectar com o banco.")));
        }
    }
?>