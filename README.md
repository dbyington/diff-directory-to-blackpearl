# diff-directory-to-blackpearl

Set these three environment variables:

`DS3_ENDPOINT` (the data path IP address of the Blackpearl)

`DS3_ACCESS_KEY` (the user’s access ID)

`DS3_SECRET_KEY` (the user’s secret key)
  
The command syntax is;

`diff-directory-to-blackpearl <local mount/directory> <Blackpearl bucket>`
 
So if the Verde volume “test1”, corresponding to the “test1” bucket on the Blackpearl, is mounted on Y: the command would be;
 
`diff-directory-to-blackpearl Y: test1`
