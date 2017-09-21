#Purpose -> gitpush in one command.
#Created on 9-NOV-2014
#Author = John Meah
#Version 1.0

echo -n "Enter the details of your deployment (i.e. 4-FEB-2014 Updating this script.) > "
read comment
echo "You entered $comment"
#set -v verbose #echo on

#Push to AWS
echo "Committing to git..."
git add --all
git commit -m "$comment"
echo "Pushing to github..."
set -v verbose #echo on
git push -f 
