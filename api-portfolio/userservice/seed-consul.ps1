# Set your Consul host
$CONSUL_HOST = "http://localhost:8500"

# Set your config prefix
$PREFIX = "config/app/api/userservice/"

# Path to your properties file
$propertiesPath = "./this.properties"

# Delete all existing keys under the prefix
Write-Host "Deleting existing keys under $PREFIX..."
$uri = "$CONSUL_HOST/v1/kv/$($PREFIX)?recurse=true"
Invoke-RestMethod -Method Delete -Uri $uri | Out-Null
Write-Host "Old keys deleted.`n"

# Read and push new keys
Get-Content $propertiesPath | ForEach-Object {
    # Skip empty lines and comments
    if ($_ -match '^\s*$' -or $_ -match '^\s*#') {
        return
    }

    # Split key and value
    $parts = $_ -split '=', 2
    if ($parts.Count -eq 2) {
        $key = $parts[0].Trim()
        $value = $parts[1].Trim()

        # Skip keys that contain 'config'
        if ($key -like '*config*') {
            return
        }

        # Encode URI components
        $encodedKey = [System.Uri]::EscapeDataString($key)
        $encodedValue = [System.Uri]::EscapeDataString($value)

        # Build full URL
        $url = "$CONSUL_HOST/v1/kv/$PREFIX$encodedKey"

        # Send to Consul KV store
        Invoke-RestMethod -Method Put -Uri $url -Body $value | Out-Null

        Write-Host "PUT: $key = $value"
    }
}

Write-Host "`nAll eligible keys pushed to Consul KV."
