
# tagging
variable "DOCKER_REGISTRY"      {default = "ghcr.io/diinnk"}
variable "IMAGE_NAME"           {default = "powlypollington"}
variable "GIT_BRANCH"           {default = "main"}

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

target "branch" {
    inherits = ["default"]
    tags = tag("${{ steps.extract_branch.outputs.branch }}")
}
