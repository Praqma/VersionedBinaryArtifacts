set -e

git fetch origin master:refs/remotes/origin/master \
&& git checkout -b master origin/master \
&& git merge develop \
&& git push "https://${GITHUB_SECRET_TOKEN}@github.com/Praqma/VersionedBinaryArtifacts.git" -u origin master
