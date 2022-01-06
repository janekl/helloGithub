# Script to fill missing commits starting from DATE_FROM to DATE_TO (inclusive)
# given as two positional arguments. If DATE_TO is not set, it defaults to today.

set -e
DIR="$(dirname "$0")"
cd $DIR

DATE_FROM=$(date -I -d "$1")

if [[ -z "$2" ]]; then
  DATE_TO=$(date '+%Y-%m-%d')
else
  DATE_TO=$(date -I -d "$2")
fi

echo "DATE_FROM = $DATE_FROM"
echo "DATE_TO = $DATE_TO"

# git pull origin master
DATE=$DATE_FROM
echo "START!"
while ! [[ "$DATE" > "$DATE_TO" ]]; do
  echo "DATE = $DATE"
  sed -i -E "s/${DATE}$/${DATE} OK/" dates.txt
  git add dates.txt
  if ! [[ -z `git status --porcelain --untracked-files=no` ]]; then
    git commit -m "$DATE" --date "$DATE"  # perhaps set hour too
  fi
  DATE=$(date -I -d "$DATE + 1 day")
done
git push origin master
