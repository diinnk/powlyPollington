
# tagging
variable "DOCKER_REGISTRY"      {default = "ghcr.io/diinnk"}
variable "IMAGE_NAME"           {default = "powlypollington"}

function "tag" {
    params = [tag]
    result = [
        "${DOCKER_REGISTRY}/${IMAGE_NAME}:${tag}"
    ]
}

group "default" {
    targets = [
        "main"
    ]
}

target "default" {
    context = "target/universal"
    dockerfile = "../../Dockerfile"
}

target "main" {
    inherits = ["default"]
    tags = tag("latest")
}

target "pr" {
    inherits = ["default"]
    tags = tag("pr")
}
