DIR="$(dirname "$0")"
TODAY=$(date '+%Y-%m-%d')

echo "Today is ${TODAY}"

cd $DIR
git pull origin master
sed -i -E "s/${TODAY}$/${TODAY} OK/" dates.txt
git add dates.txt
git commit -m "${TODAY}"
git push origin master
