def cmd(command)
  puts command
  puts `#{command}`
end

args = ARGV
if args.size<2
  puts "Usage: ruby cherry.rb <commit> <start-branch> [<end-branch>]"
else
  commit = ARGV[0]
  MIN_BRANCH = 0
  MAX_BRANCH = 8
  start_branch = ARGV[1].to_i
  end_branch = ARGV[2]==nil ?  MAX_BRANCH : ARGV[2].to_i
  if start_branch<MIN_BRANCH or start_branch>MAX_BRANCH
    puts "invalid start branch:#{start_branch}, should be between #{MIN_BRANCH} and #{MAX_BRANCH}"
  else
    puts "Cherry picking #{commit} into branchs #{start_branch}..#{end_branch}"
    (start_branch..end_branch).each do |t|
      cmd "git co #{t}"
      cmd "git cherry-pick #{commit}"
    end
  end
end
