# Set Consul host
$CONSUL_HOST = "http://localhost:8500"

# KV prefix path
$PREFIX = "config/infra/mongodb/"

# MongoDB configuration
$mongoConfig = @{
    "spring.data.mongodb.host" = "localhost"
    "spring.data.mongodb.port" = "27017"
    "spring.data.mongodb.database" = "portfolio"
    "spring.data.mongodb.username" = "test"
    "spring.data.mongodb.password" = "test"
    "spring.data.mongodb.uri" = "mongodb://test:test@localhost:27017/admin?retryWrites=true&loadBalanced=false&connectTimeoutMS=10000&authSource=admin&authMechanism=SCRAM-SHA-1"
    "spring.data.mongodb.uuid-representation" = "standard"
}

# Optional: delete old keys at this path
$uri = "$CONSUL_HOST/v1/kv/$($PREFIX)?recurse=true"
Invoke-RestMethod -Method Delete -Uri $uri | Out-Null
Write-Host "Deleted existing keys under $PREFIX"

# Upload each key to Consul
$mongoConfig.Keys | ForEach-Object {
    $key = $_
    $value = $mongoConfig[$_]
    $url = "$CONSUL_HOST/v1/kv/$PREFIX$key"
    
    Invoke-RestMethod -Method Put -Uri $url -Body $value | Out-Null
    Write-Host "PUT $PREFIX$key = $value"
}

Write-Host "`n MongoDB KV config uploaded to Consul successfully!"
