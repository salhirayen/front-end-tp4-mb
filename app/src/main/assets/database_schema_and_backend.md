# TP4 Backend & Database Documentation (Activité 1)

## 1. MySQL Database Update (`users` table)
Pour répondre à l'**Activité 1**, la table `users` de la base de données `tpdatabase` doit être mise à jour pour inclure les nouveaux champs : `nom`, `prénom`, `email`, et `téléphone`.

```sql
ALTER TABLE users ADD COLUMN nom VARCHAR(100) NOT NULL;
ALTER TABLE users ADD COLUMN prenom VARCHAR(100) NOT NULL;
ALTER TABLE users ADD COLUMN email VARCHAR(150) NOT NULL;
ALTER TABLE users ADD COLUMN telephone VARCHAR(20) NOT NULL;
```

Ou création complète de la table :
```sql
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL,
    telephone VARCHAR(20) NOT NULL
);
```

---

## 2. Updated `user.php`
Ajout de la méthode `registerUser` dans la classe `User` :

```php
<?php
include_once 'db-connect.php';

class User {
    private $db;
    private $db_table = "users";

    public function __construct() {
        $this->db = new DbConnect();
    }

    public function isLoginExist($username, $password) {
        $query = "select * from ".$this->db_table." where username = '$username' AND password = '$password' Limit 1";
        $result = mysqli_query($this->db->getDb(), $query);
        if(mysqli_num_rows($result) > 0) {
            mysqli_close($this->db->getDb());
            return true;
        }
        mysqli_close($this->db->getDb());
        return false;
    }

    public function loginUsers($username, $password) {
        $json = array();
        $canUserLogin = $this->isLoginExist($username, $password);
        if($canUserLogin) {
            $json['success'] = 1;
            $json['message'] = "Successfully logged in";
        } else {
            $json['success'] = 0;
            $json['message'] = "Incorrect details";
        }
        return $json;
    }

    public function registerUser($username, $password, $nom, $prenom, $email, $telephone) {
        $json = array();
        
        // Vérifier si le username existe déjà
        $queryCheck = "SELECT * FROM ".$this->db_table." WHERE username = '$username' LIMIT 1";
        $resultCheck = mysqli_query($this->db->getDb(), $queryCheck);
        
        if(mysqli_num_rows($resultCheck) > 0) {
            $json['success'] = 0;
            $json['message'] = "Username already exists";
        } else {
            $query = "INSERT INTO ".$this->db_table." (username, password, nom, prenom, email, telephone) VALUES ('$username', '$password', '$nom', '$prenom', '$email', '$telephone')";
            $inserted = mysqli_query($this->db->getDb(), $query);
            
            if($inserted) {
                $json['success'] = 1;
                $json['message'] = "User registered successfully";
            } else {
                $json['success'] = 0;
                $json['message'] = "Registration failed";
            }
        }
        mysqli_close($this->db->getDb());
        return $json;
    }
}
?>
```

---

## 3. New `register.php` Endpoint
Script PHP pour gérer la requête d'inscription (`register.php`) :

```php
<?php
require_once 'user.php';

$data = file_get_contents('php://input');
$obj = json_decode($data, true);

$username = isset($obj['username']) ? $obj['username'] : "";
$password = isset($obj['password']) ? $obj['password'] : "";
$nom = isset($obj['nom']) ? $obj['nom'] : "";
$prenom = isset($obj['prenom']) ? $obj['prenom'] : "";
$email = isset($obj['email']) ? $obj['email'] : "";
$telephone = isset($obj['telephone']) ? $obj['telephone'] : "";

$userObject = new User();

if(!empty($username) && !empty($password) && !empty($nom) && !empty($prenom) && !empty($email) && !empty($telephone)) {
    $json_array = $userObject->registerUser($username, $password, $nom, $prenom, $email, $telephone);
    echo json_encode($json_array);
} else {
    echo json_encode(array("success" => 0, "message" => "All fields are required"));
}
?>
```
