{
  description = "intellij-haskell-lsp development environment";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";
    flake-utils.url = "github:numtide/flake-utils";
  };

  outputs = { self, nixpkgs, flake-utils }:
    flake-utils.lib.eachDefaultSystem (system:
      let
        pkgs = nixpkgs.legacyPackages.${system};
      in {
        devShells.default = pkgs.mkShell {
          name = "intellij-haskell-lsp";

          packages = with pkgs; [
            jdk17
            gradle
            kotlin
          ];

          JAVA_HOME = "${pkgs.jdk17}";

          shellHook = ''
            echo "intellij-haskell-lsp dev shell"
            echo "Java: $(java -version 2>&1 | head -1)"
            echo "Gradle: $(gradle --version 2>/dev/null | head -1)"
          '';
        };
      });
}
